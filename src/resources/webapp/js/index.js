//If they are coming here, then we should be wiping the slate clean
sessionStorage.clear();

document.addEventListener("DOMContentLoaded", () => {
    const loginButton = document.getElementById("submitButton");
    const usernameField = document.getElementById("usernameField");

    //Check whether the username is valid if they leave the box
    usernameField.addEventListener("blur",(e) =>{
        const username = document.getElementById("usernameField").value;
        fetch("http://localhost:7000/usernameAvailable/?username=" + username).then(response =>{
            response.text().then(function (text) {
                // do something with the text response
                if(!text.includes("unavailable")){
                    usernameField.classList.add("wrong");
                    usernameField.classList.remove("input-valid");
                }
                else{
                    usernameField.classList.remove("wrong");
                    usernameField.classList.add("input-valid")
                }
            });
        });
    });

    loginButton.addEventListener("click",(e) => {
        e.preventDefault();
        const username = document.getElementById("usernameField").value;
        const password = document.getElementById("passwordField").value;
        fetch("http://localhost:7000/login/",{
            method:"post",
            mode:"cors",
            body:JSON.stringify({
                "username": username,
                "password": password
            })
        }).then(response => {
            sessionStorage.setItem("user",username);
            if(response.url.includes("Manager")){
                sessionStorage.setItem("manager","true");
            }
            window.location.replace(response.url);
        });

    })});