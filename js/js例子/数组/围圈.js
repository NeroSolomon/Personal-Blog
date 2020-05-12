function LastRemaining_Solution(n, m)
{
  if(n==0){
    return -1
  }
  if(n==1){
      return 0;
  }
  else{
    return  (LastRemaining_Solution(n-1,m)+m)%n;
  }
}

console.log(LastRemaining_Solution(5, 2))