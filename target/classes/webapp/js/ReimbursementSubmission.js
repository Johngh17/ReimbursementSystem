const loggedUser = sessionStorage.getItem("user");

if(sessionStorage.getItem("Manager") == "true"){

}

document.addEventListener("DOMContentLoaded", () => {
    const amountField = document.getElementById("Amount");
    const locationField = document.getElementById("Location");
    const businessField = document.getElementById("Business");
    const submitButton = document.getElementById("Submit");

    submitButton.addEventListener("click", function(e){
        e.preventDefault();
        fetch("http://localhost:7000/reimbursementSubmission/",{
            method:"POST",
            mode:"cors",
            body:JSON.stringify({
                username:loggedUser,
                amount:amountField.value,
                establishment:businessField.value,
                location:locationField.value
            })
        }).then(function(response){
            alert("Successfully submitted request");
            if(sessionStorage.getItem("admin") === "true"){
                window.location.replace("http://localhost:7000/EmployeeHomepageManager.html");
            }
            else{
                window.location.replace("http://localhost:7000/EmployeeHomepage.html");
            }
        });
    })
});