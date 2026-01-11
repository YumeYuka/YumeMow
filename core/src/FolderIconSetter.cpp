#include "FolderIconSetter.hpp"

#include <QFileInfo>
#include <QDebug>

#ifdef _WIN32
#include <shlobj.h>
#include <windows.h>
#endif

FolderIconResult FolderIconSetter::setIcon(const QString& folderPath,
                                          const QString& iconPath,
                                          const int iconIndex) noexcept {
    try {
        if (!QFileInfo::exists(folderPath)) {
            return {FolderIconError::FolderNotFound, QStringLiteral("Folder not found: %1").arg(folderPath)};
        }

        if (!QFileInfo::exists(iconPath)) {
            return {FolderIconError::IconNotFound, QStringLiteral("Icon not found: %1").arg(iconPath)};
        }

#ifdef _WIN32
        SHFOLDERCUSTOMSETTINGS fcs = {sizeof(SHFOLDERCUSTOMSETTINGS)};
        fcs.dwMask = FCSM_ICONFILE;

        const auto iconWStr = iconPath.toStdWString();
        fcs.pszIconFile = const_cast<LPWSTR>(iconWStr.c_str());
        fcs.iIconIndex = iconIndex;

        const HRESULT hr = SHGetSetFolderCustomSettings(&fcs, folderPath.toStdWString().c_str(), FCS_FORCEWRITE);

        if (SUCCEEDED(hr)) {
            SHChangeNotify(SHCNE_ATTRIBUTES, SHCNF_PATHW, folderPath.toStdWString().c_str(), nullptr);
            return {FolderIconError::Success, QString()};
        }

        const QString errorMsg = QStringLiteral("HRESULT: 0x%1").arg(quint32(hr), 8, 16, QChar('0'));
        return {FolderIconError::WriteFailed, QStringLiteral("Failed to set icon: ").arg(errorMsg)};
#else
        return {FolderIconError::NotSupported, QStringLiteral("Folder icon setting is only supported on Windows")};
#endif
    } catch (const std::exception& e) {
        return {FolderIconError::InvalidPath, QString::fromUtf8(e.what())};
    } catch (...) {
        return {FolderIconError::InvalidPath, QStringLiteral("Unknown error occurred")};
    }
}
