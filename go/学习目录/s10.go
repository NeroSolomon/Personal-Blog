package main

import "fmt"

func main() {
	m := map[string]int{"a": 1, "b": 2}
	v1 := m["a"]
	m["b"] = 3
	fmt.Println(v1, m["b"], len(m))

	for k, v := range m {
		fmt.Println(k, v)
	}

	delete(m, "a")
	fmt.Println(m)
}
