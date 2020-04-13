    var type = 'images';
    var size = {width: 800, height: 600};
    var format = ['jpg', 'png'];

    function change(type, size, format){
        type = 'video';
        size = {width: 1024, height: 768};
        format.push('map');
    }

    change(type, size, format);

    console.log(type, size, format);
    // 'images' {width: 800, height: 600} ['jpg', 'png', 'map']


    /**
      1.type
      函数的参数是存在独立的作用域中
      实际上 change(type) = change(var type = type)
      所以内部作用域的type发生了改变不会影响外部作用域

      2.size
      change(size) = change(var size=size)
      内部作用域的size保存了指向栈中指向堆内的指针
      size = {width: 1024, height: 768} 实际上是将另外一个对象的指针赋值给内部的size，而外部的size仍然存着原来的指针

      3.format
      change(format) = change(var format=format)
      内部作用域的format保存了指向栈中指向堆内的指针
      format.push没有改变指针，所以可以操作堆中的数组
     */