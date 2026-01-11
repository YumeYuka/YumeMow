#include "FileDialog.hpp"
#include <QStringList>

#ifdef _WIN32
#include <shlobj.h>
#include <shobjidl.h>
#include <comdef.h>
#include <memory>

/* The `ComPtr` class is a smart pointer implementation for managing COM interface pointers in C++. Here's a breakdown of
its functionality: */
template<typename T>
class ComPtr {
    T* ptr_ = nullptr;
public:
    ComPtr() = default;
    explicit ComPtr(T* p) : ptr_(p) {}
    ~ComPtr() { if (ptr_) ptr_->Release(); }
    T** operator&() { release(); return &ptr_; }
    T* operator->() { return ptr_; }
    operator bool() const { return ptr_ != nullptr; }
    void release() { if (ptr_) { ptr_->Release(); ptr_ = nullptr; } }
    T* get() { return ptr_; }
};

/* The code snippet `static thread_local wchar_t* filterName = nullptr; static thread_local wchar_t* filterSpec = nullptr;`
is declaring two thread-local static variables `filterName` and `filterSpec` of type `wchar_t*` and initializing them to
`nullptr`. */
static thread_local wchar_t* filterName = nullptr;
static thread_local wchar_t* filterSpec = nullptr;

FileDialogResult FileDialog::selectFolder() {
    FileDialogResult result{QString(), true};

    HRESULT hr = CoInitializeEx(nullptr, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    bool comInitialized = SUCCEEDED(hr);

    ComPtr<IFileOpenDialog> dialog;
    hr = CoCreateInstance(CLSID_FileOpenDialog, nullptr, CLSCTX_ALL,
                          IID_IFileOpenDialog, reinterpret_cast<void**>(&dialog));

    if (SUCCEEDED(hr)) {
        DWORD options;
        dialog->GetOptions(&options);
        dialog->SetOptions(options | FOS_PICKFOLDERS | FOS_FORCEFILESYSTEM);

        hr = dialog->Show(nullptr);

        if (SUCCEEDED(hr)) {
            ComPtr<IShellItem> item;
            hr = dialog->GetResult(&item);

            if (SUCCEEDED(hr)) {
                PWSTR path = nullptr;
                hr = item->GetDisplayName(SIGDN_FILESYSPATH, &path);

                if (SUCCEEDED(hr)) {
                    result.path = QString::fromWCharArray(path);
                    result.cancelled = false;
                    CoTaskMemFree(path);
                }
            }
        }
    }

    if (comInitialized) CoUninitialize();
    return result;
}

/**
 * The function `openFile` in the `FileDialog` class initializes a COM environment, creates a file open dialog, sets
 * options and filters, displays the dialog, retrieves the selected file path, and returns the result.
 * 
 * @param filter The `filter` parameter in the `openFile` function is a QString that represents the file filter to be
 * applied when selecting a file using the file dialog. The filter string should be in the format
 * "Name1|Spec1|Name2|Spec2|...", where NameX represents the display
 * 
 * @return The function `openFile` returns a `FileDialogResult` object, which contains information about the selected file
 * path and whether the file dialog was cancelled or not.
 */
FileDialogResult FileDialog::openFile(const QString& filter) {
    FileDialogResult result{QString(), true};

    HRESULT hr = CoInitializeEx(nullptr, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
    bool comInitialized = SUCCEEDED(hr);

    ComPtr<IFileOpenDialog> dialog;
    hr = CoCreateInstance(CLSID_FileOpenDialog, nullptr, CLSCTX_ALL,
                          IID_IFileOpenDialog, reinterpret_cast<void**>(&dialog));

    if (SUCCEEDED(hr)) {
        dialog->SetOptions(FOS_FORCEFILESYSTEM);

        if (!filter.isEmpty()) {
            QStringList parts = filter.split('|');
            if (parts.size() >= 2) {
                QString name = parts[0];
                QString spec = parts[1];

                filterName = new wchar_t[name.length() + 1];
                name.toWCharArray(filterName);
                filterName[name.length()] = L'\0';

                filterSpec = new wchar_t[spec.length() + 1];
                spec.toWCharArray(filterSpec);
                filterSpec[spec.length()] = L'\0';

                COMDLG_FILTERSPEC filters[] = {{filterName, filterSpec}};
                dialog->SetFileTypes(1, filters);
            }
        }

        hr = dialog->Show(nullptr);

        if (SUCCEEDED(hr)) {
            ComPtr<IShellItem> item;
            hr = dialog->GetResult(&item);

            if (SUCCEEDED(hr)) {
                PWSTR path = nullptr;
                hr = item->GetDisplayName(SIGDN_FILESYSPATH, &path);

                if (SUCCEEDED(hr)) {
                    result.path = QString::fromWCharArray(path);
                    result.cancelled = false;
                    CoTaskMemFree(path);
                }
            }
        }
    }

    if (filterName) { delete[] filterName; filterName = nullptr; }
    if (filterSpec) { delete[] filterSpec; filterSpec = nullptr; }

    if (comInitialized) CoUninitialize();
    return result;
}

#else

FileDialogResult FileDialog::selectFolder() {
    return {QString(), true};
}

FileDialogResult FileDialog::openFile(const QString& filter) {
    Q_UNUSED(filter)
    return {QString(), true};
}

#endif
