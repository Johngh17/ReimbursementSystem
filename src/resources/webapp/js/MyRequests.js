const loggedUser = sessionStorage.getItem("user");

var tableDiv = document.createElement("div");
tableDiv.id = "tableRoot";
document.body.appendChild(tableDiv);

var resetTable = function (){
    while(tableDiv.firstChild){
        tableDiv.removeChild(tableDiv.firstChild);
    }
}

async function getRequests() {
    const requests = await fetch("http://localhost:7000/employeeReimbursements/?username=" + loggedUser +
        "&pending=" + document.getElementById("pending").checked +
        "&approved=" + document.getElementById("approved").checked +
        "&denied=" + document.getElementById("denied").checked)
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

document.addEventListener("DOMContentLoaded", () => {
    const pendingBox = document.getElementById("pending");
    const approvedBox = document.getElementById("approved");
    const deniedBox = document.getElementById("denied");

    getRequests();

    pendingBox.addEventListener("change",()=>{
        getRequests();
    });

    approvedBox.addEventListener("change",()=>{
        getRequests();
    });

    deniedBox.addEventListener("change",() =>{
       getRequests();
    });

});