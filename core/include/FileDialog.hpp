#pragma once

#include <QString>

#ifdef _WIN32
#include <windows.h>
#endif

struct FileDialogResult {
    QString path;
    bool cancelled;
};

class FileDialog {
public:
    static FileDialogResult selectFolder();

    static FileDialogResult openFile(const QString& filter = QString());
};
