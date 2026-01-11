#include "FolderIconSetter.hpp"

#include <QCoreApplication>
#include <QFileInfo>
#include <QDebug>

int main(const int argc, char* argv[]) {
    QCoreApplication app(argc, argv);

    if (argc < 3) {
        qWarning() << "Usage: YumeMowCLI <folderPath> <iconPath> [iconIndex]";
        qWarning() << "Return codes: 0=Success, 1=FolderNotFound, 2=IconNotFound, 3=InvalidPath, 4=WriteFailed, 5=NotSupported";
        return static_cast<int>(FolderIconError::InvalidPath);
    }

    const QString folderPath = QString::fromLocal8Bit(argv[1]);
    const QString iconPath = QString::fromLocal8Bit(argv[2]);
    const int iconIndex = argc >= 4 ? QString::fromLocal8Bit(argv[3]).toInt() : 0;

    const auto result = FolderIconSetter::setIcon(folderPath, iconPath, iconIndex);

    if (!result.isSuccess()) {
        qWarning() << "Error:" << result.message;
        return static_cast<int>(result.code);
    }

    qInfo() << "Successfully set icon for folder:" << folderPath;
    return 0;
}
