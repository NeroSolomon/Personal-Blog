function cutRope(number)
{
    // write code here
    var max = 1;
    if(number<=3 && number>0){
        return number-1;
    }
    while(number>4){
        number-=3;
        max*=3;
    }
    return max*number;
}

console.log(cutRope(7));
