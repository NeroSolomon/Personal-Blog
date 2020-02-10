function replace(str) {
  if (typeof str != 'string') return
  str = str.replace(/\s/g, '%20')
  console.log(str)
}

replace('Hello world 2')