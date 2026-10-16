#include "file_slice.h"
#include <cassert>
#include <cstdio>
#include <fcntl.h>
#include <string>

int main() {
    FILE *file = tmpfile();
    assert(file);
    int fd = fileno(file);
    assert(write(fd, "prefixPAYLOADsuffix", 19) == 19);
    assert(lseek(fd, 3, SEEK_SET) == 3);
    FileSlice slice;
    assert(slice.open(fd, 6, 7));
    char data[64]{};
    assert(slice.read(data, sizeof(data)) == 7);
    assert(std::string(data, 7) == "PAYLOAD");
    assert(slice.read(data, 1) == 0); // Never read the suffix of an Android resource.
    assert(lseek(fd, 0, SEEK_CUR) == 3); // dup alone would share the caller's offset.
    assert(slice.seek(-1) < 0 && slice.seek(8) < 0);
    assert(slice.seek(2) == 2);
    assert(slice.read(data, 2) == 2 && std::string(data, 2) == "YL");
    FileSlice other;
    assert(other.open(fd, 6, 7));
    assert(other.read(data, 2) == 2 && std::string(data, 2) == "PA");
    fclose(file); // The caller may close the original descriptor immediately.
    assert(slice.seek(0) == 0);
    assert(slice.read(data, 7) == 7 && std::string(data, 7) == "PAYLOAD");
    slice.cancelled = true;
    assert(slice.read(data, 1) < 0 && slice.seek(0) < 0);
    FileSlice invalid;
    assert(!invalid.open(-1, 0, -1));
    FILE *large = tmpfile();
    assert(large);
    constexpr int64_t start = (int64_t{1} << 32) + 7;
    assert(pwrite(fileno(large), "64bit", 5, start) == 5);
    FileSlice tail;
    assert(tail.open(fileno(large), start, -1));
    assert(tail.length == 5 && tail.read(data, 64) == 5);
    assert(std::string(data, 5) == "64bit");
    FileSlice overflow;
    assert(!overflow.open(fileno(large), INT64_MAX, 1));
    fclose(large);
    puts("FileSlice: bounds, independent offsets, ownership, cancellation and >4GB offsets passed");
}
