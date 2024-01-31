package main

import (
	"fmt"
	"strconv"
)

func main() {
	var a int = 10
	var b float64 = float64(a)

	var str string = "5"
	var c int = int(b)
	var d int
	// strconv.Atoi 函数返回两个值，第一个是转换后的整型值，第二个是可能发生的错误，我们可以使用空白标识符 _ 来忽略这个错误
	d, _ = strconv.Atoi(str)

	e := strconv.Itoa(a)

	fmt.Println(str, c, d, e)
}
