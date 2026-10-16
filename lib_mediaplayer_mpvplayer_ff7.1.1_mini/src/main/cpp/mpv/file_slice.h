#pragma once

#include <algorithm>
#include <atomic>
#include <cerrno>
#include <cstdint>
#include <limits>
#include <sys/stat.h>
#include <unistd.h>

// pread keeps each mpv stream independent of the caller's file position.
struct FileSlice {
    int fd = -1;
    int64_t offset = 0, length = 0, position = 0;
    std::atomic<bool> cancelled{false};
    ~FileSlice() { if (fd >= 0) close(fd); }

    bool open(int source, int64_t start, int64_t size) {
        struct stat st{};
        if (start < 0 || size < -1 || fstat(source, &st) != 0 || !S_ISREG(st.st_mode) || start > st.st_size)
            return false;
        offset = start;
        length = size < 0 ? st.st_size - start : std::min<int64_t>(size, st.st_size - start);
        fd = dup(source);
        return fd >= 0;
    }
    int64_t read(char *buffer, uint64_t requested) {
        if (cancelled) return -1;
        uint64_t count = std::min<uint64_t>(requested, static_cast<uint64_t>(length - position));
        count = std::min<uint64_t>(count, std::numeric_limits<ssize_t>::max());
        ssize_t result;
        do { result = pread(fd, buffer, count, offset + position); } while (result < 0 && errno == EINTR && !cancelled);
        if (result > 0) position += result;
        return result;
    }
    int64_t seek(int64_t target) {
        if (cancelled || target < 0 || target > length) return -1;
        return position = target;
    }
};
