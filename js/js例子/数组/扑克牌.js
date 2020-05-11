function IsContinuous(numbers)
{
    // write code here
    let times = [-3];
    let min = 14;
    let max = -1;
    let len = numbers.length;
    if (len != 5) return false;
    for (let i = 0; i < 14; i++) {
        times.push(0);
    }
    
    for (let i = 0; i < len; i++) {
        times[numbers[i]]++;
        if (numbers[i] == 0) continue
        if (times[numbers[i]] > 1) return false
        if (numbers[i] > max) {
            max = numbers[i]
        }
        if (numbers[i] < min) {
            min = numbers[i]
        }
    }
    
    return max - min < 5
}

console.log(IsContinuous([1,0,0,1,0]))
