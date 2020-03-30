function Merge(pHead1, pHead2)
{
    // write code here
    let list = {}
    if(pHead1 === null){
        return pHead2;
    } else if (pHead2 === null) {
        return pHead1;
    }
    if(pHead1 > pHead2){
        list = pHead2;
        list.next = Merge(pHead2.next, pHead1);
    } else {
        list = pHead1;
        list.next = Merge(pHead2, pHead1.next)
    }
    return list;
}