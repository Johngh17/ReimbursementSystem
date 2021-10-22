const loggedUser = sessionStorage.getItem("user");

if(sessionStorage.getItem("manager") == "true"){
    window.location.replace("http://localhost:7000/EmployeeHomepageManager.html");
}

var tableDiv = document.createElement("div");
tableDiv.id = "tableRoot";
document.body.appendChild(tableDiv);

function resetTable(){
    while(tableDiv.firstChild){
        tableDiv.removeChild(tableDiv.firstChild);
    }
}

async function getRequests() {
    const requests = await fetch("http://localhost:7000/employeeReimbursements/?username=" + loggedUser +
        "&pending=true" +
        "&approved=false"+
        "&denied=false")
        .then(response =>
            response.json());

    resetTable();

    if(requests.length > 0){
        var table = document.createElement("table");
        table.id="resultsTable";

        var headers = ["ID","Requester","Amount","Business","Location","Status"];
        var headerSection = document.createElement("tr");
        table.appendChild(headerSection);
        for(let i = 0; i < headers.length; i++){
            var j = document.createElement("th");
            j.textContent = headers[i] + "  ";
            headerSection.appendChild(j);
        }
    }
    for(let i = 0; i < requests.length; i++){
        let row = document.createElement("tr");

        let id = document.createElement("td");
        id.textContent = requests[i]["id"];
        row.appendChild(id);

        let requester = document.createElement("td");
        requester.textContent = requests[i]["requester"]["firstname"] + " " + requests[i]["requester"]["lastname"];
        row.appendChild(requester);

        let amount = document.createElement("td");
        amount.textContent = requests[i]["amount"];
        row.appendChild(amount);

        let establishment = document.createElement("td");
        establishment.textContent = requests[i]["establishment"];
        row.appendChild(establishment);

        let location = document.createElement("td");
        location.textContent = requests[i]["location"];
        row.appendChild(location);

        let status = document.createElement("td");
        status.textContent = requests[i]["status"];
        row.appendChild(status);

        table.appendChild(row);
    }
    tableDiv.appendChild(table);
}

document.getElementById("WelcomeMessage").innerHTML =
    document.getElementById("WelcomeMessage").innerHTML + ", " +loggedUser;

document.addEventListener("DOMContentLoaded", () => {
    getRequests();
});