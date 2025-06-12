openMenu=0
document.querySelector(".menu-burger").onclick=function (){
    if(openMenu){
        document.querySelector(".menu-header").style.top='-200%'
        document.querySelector(".menu-burger .center").style=''
        document.querySelector('.menu-burger .after').style=''
        document.querySelector('.menu-burger .before').style=''
        document.body.style.overflow='visible'
    }
    else {
        document.querySelector(".menu-header").style.top='0'
        document.querySelector(".menu-burger .center").style.display="none"
        document.querySelector('.menu-burger .after').style.transform='rotate(-45deg)'
        document.querySelector('.menu-burger .before').style.transform='rotate(45deg)'
        document.querySelector('.menu-burger .before').style.height='5px'
        document.querySelector('.menu-burger .after').style.height='5px'
        document.querySelector('.menu-burger .after').style.top='15px'
        document.querySelector('.menu-burger .before').style.top='15px'
        document.querySelector('.menu-burger .after').style.backgroundColor='red'
        document.querySelector('.menu-burger .before').style.backgroundColor='red'
        document.querySelector('.menu-burger .before').style.backgroundColor='red'
        document.body.style.overflow='hidden'

    }
    openMenu=!openMenu
}