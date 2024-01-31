package main

// 声明包名

import "fmt"

// 每一个可执行程序必须包含main
func main() {
	fmt.Println("Hello," + " world")
	var stockcode = 123
	var enddate = "2020-12-31"
	var url = "code=%d&enddate=%s"
	var target_url = fmt.Sprintf(url, stockcode, enddate)
	fmt.Println(target_url)
}
