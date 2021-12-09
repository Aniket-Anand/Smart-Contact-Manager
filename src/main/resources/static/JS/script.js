console.log("This is script file");

const toggleSidebar =()=>{
    if($(".sidebar").is(":visible"))
    {
        //true
        //band krna h

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");

    }
    else{
        //false
        //show krna hai
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");

    }

};