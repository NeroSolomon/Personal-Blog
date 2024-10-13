function compareVersion(version1, version2) {
    const v1 = version1.split(".");
    const v2 = version2.split(".");

    return compareRecursive(v1, v2, 0);

    function compareRecursive(v1, v2, index) {
        if (index >= Math.max(v1.length, v2.length)) {
            return 0;
        }

        const num1 = parseInt(v1[index] || 0);
        const num2 = parseInt(v2[index] || 0);

        if (num1 > num2) {
            return 1;
        } else if (num1 < num2) {
            return -1;
        }

        return compareRecursive(v1, v2, index + 1);
    }
}