const username = sessionStorage.getItem("user");

document.addEventListener("DOMContentLoaded", () => {
    const submitButton = document.getElementById("submitButton");
    const firstname = document.getElementById("firstname");
    const lastname = document.getElementById("lastname");
    submitButton.addEventListener("click",() => {
        fetch("http://localhost:7000/updateInfo/",{
            mode: "cors",
            method:"post",
            body: JSON.stringify({
                authority:username,
                username:username,
                firstname:firstname.value,
                lastname:lastname.value
            })
        }).then(window.location.replace("http://localhost:7000/EmployeeHomepage.html"));
    });
});