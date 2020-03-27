function cutRope(number)
{
    // write code here
    let result = [];
    let answer = [0, 0, 1, 2, 4];
    if(number < 5) {
        return answer[number];
    }
    while(number > 0) {
        if(number - 3 >= 0) {
            result.push(3);
            number -= 3;
        }
        if(number - 2 >= 0) {
            result.push(2);
            number -= 2;
        }
    }
    return result.reduce((x, y) => x * y);
}

console.log(cutRope(5));