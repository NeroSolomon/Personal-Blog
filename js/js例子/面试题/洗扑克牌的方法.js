let Shuffle = () =>  
    [...'A23456789JQK',10]
    .reduce( (arr, e) => arr.concat([`♥${e}`,`♠${e}`,`♣${e}`,`♦${e}`]), ['大王','小王'])
    .sort( () => Math.floor(Math.random()*2-Math.random()*2))