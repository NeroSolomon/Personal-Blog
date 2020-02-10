function doubleAfter2seconds(num) {
  return new Promise((resolve, reject) => {
      setTimeout(() => {
          resolve(2 * num)
      }, 2000);
  } )
}

async function AsFun() {
  let num1 = await doubleAfter2seconds(50)
  let num2 = await doubleAfter2seconds(100)
  console.log(num1)
  console.log(num2)
  console.log('after async')
}

// async function AsFun() {
//   let num = await setTimeout(() => {
//       console.log('1')
//       num = 100
//   }, 2000);
//   console.log(num)
//   console.log('after async')
// }

AsFun();
console.log('outer');