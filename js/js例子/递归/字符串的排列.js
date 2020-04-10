function Permutation(str){
  let res=[]
  if(str.length==1){
    res.push(str)
  }else{
    let obj={}
    for(let i=0;i<str.length;i++){
      let c=str[i]
      if(!obj[c]){
        let newStr=str.slice(0,i)+str.slice(i+1)
        let l=Permutation(newStr)
        for(var j=0;j<l.length;j++){
          res.push(c+l[j])
        }
        obj[c]=true
      }
    }
  }
  return res
}

const result = Permutation('abc');
console.log(Permutation('abc'));