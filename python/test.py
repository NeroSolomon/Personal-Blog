# -*- coding: UTF-8 -*-
import math

def print_func (par):
    print "Hello:", par
    return

a = 'ner'

# global 关键词
Money = 2000
def AddMoney():
    global Money
    Money = 2
    print Money

# print Money
# AddMoney()
# print Money

# dir 函数
content = dir(math)
# print content

# # globals() 和 locals() 函数
def globals_test():
    global Money
    b = 'test'
    g_var = globals()
    l_var = locals()
    print g_var
    print l_var

# globals_test()