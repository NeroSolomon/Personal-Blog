package main

import "fmt"

// 接口
type Phone interface {
	call()
}

// 结构体，实现接口
type NokiaPhone struct{}

func (nokiaPhone NokiaPhone) call() {
	fmt.Println("I am Nokia, I can call you.")
}

type IPhone struct{}

func (iPhone IPhone) call() {
	fmt.Println("I am iPhone, I can call you.")
}

func main() {
	var p Phone

	p = new(NokiaPhone)
	p.call()

	p = new(IPhone)
	p.call()
}
