let buttons=document.querySelectorAll(".service button")
let blocks=document.querySelectorAll(".service")
for(var i=0;i<blocks.length;i++){
    blocks[i].addEventListener("click",evt => {


        if(evt.target!==evt.currentTarget.querySelector("button")){
            window.location.href=evt.currentTarget.querySelector("a").href
        }
    })
    buttons[i].onclick=buttonsClick
}
function buttonsClick(env){
    window.location.href=this.querySelector('a').href
}

