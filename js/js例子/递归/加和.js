function Sum_Solution(n)
{
    // write code here
    if (n == 1) return 1
    return n + Sum_Solution(n - 1)
}