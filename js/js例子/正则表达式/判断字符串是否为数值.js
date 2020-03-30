//s字符串
function isNumeric(s)
{
    // write code here
    return s.match(/[+-]?\d*(\.\d*)?([eE][+-]?\d+)?/g)[0] === s
}