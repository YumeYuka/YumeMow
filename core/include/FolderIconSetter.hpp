#ifndef FOLDERICONSETTER_HPP
#define FOLDERICONSETTER_HPP

#include <QString>
#include <expected>

enum class FolderIconError {
    Success = 0,
    FolderNotFound,
    IconNotFound,
    InvalidPath,
    WriteFailed,
    NotSupported
};

struct FolderIconResult {
    FolderIconError code;
    QString message;

    [[nodiscard]] bool isSuccess() const noexcept { return code == FolderIconError::Success; }
    [[nodiscard]] explicit operator bool() const noexcept { return isSuccess(); }
};

class FolderIconSetter {
public:
    [[nodiscard]] static FolderIconResult setIcon(const QString& folderPath,
                                                  const QString& iconPath,
                                                  const int iconIndex = 0) noexcept;
};

#endif
