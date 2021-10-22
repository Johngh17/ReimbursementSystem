const loggedIn = sessionStorage.getItem("user");

document.addEventListener("DOMContentLoaded", () => {
    const submitButton = document.getElementById("submitButton");
    const firstname = document.getElementById("firstname");
    const lastname = document.getElementById("lastname");
    const username = document.getElementById("toUpdate");

    username.addEventListener("blur",() =>{
        const usernameVal = document.getElementById("toUpdate").value;
        fetch("http://localhost:7000/usernameAvailable/?username=" + usernameVal).then(response =>{
            response.text().then(function (text) {
                // do something with the text response
                if(!text.includes("unavailable")){
                    username.classList.add("wrong");
                    username.classList.remove("input-valid");
                }
                else{
                    username.classList.remove("wrong");
                    username.classList.add("input-valid")
                }
            });
        });
    })

    submitButton.addEventListener("click",() => {
        fetch("http://localhost:7000/updateInfo/",{
            mode: "cors",
            method:"post",
            body: JSON.stringify({
                authority:loggedIn,
                username:username.value,
                firstname:firstname.value,
                lastname:lastname.value
            })
        }).then(window.location.replace("http://localhost:7000/EmployeeHomepageManager.html"));
    });
});