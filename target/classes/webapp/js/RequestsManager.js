const loggedUser = sessionStorage.getItem("user");

var tableDiv = document.createElement("div");
tableDiv.id = "tableRoot";
document.body.appendChild(tableDiv);

function resetTable(){
    while(tableDiv.firstChild){
        tableDiv.removeChild(tableDiv.firstChild);
    }
}


async function getRequestsByUser() {
    const requests = await fetch("http://localhost:7000/employeeReimbursements/?" +
        "username=" + document.getElementById("user").value +
        "&pending=" + document.getElementById("pending").checked +
        "&approved=" + document.getElementById("approved").checked +
        "&denied=" + document.getElementById("denied").checked)
        .then(response =>
            response.json());

    resetTable();

    if(requests.length > 0){
        var table = document.createElement("table");
        table.id="resultsTable";

        var headers = ["ID","Requester","Amount","Business","Location","Reviewer","Status"];
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

        let reviewer = document.createElement("td");
        reviewer.textContent = requests[i]["reviewer"]["firstname"] + " " + requests[i]["reviewer"]["lastname"];
        row.appendChild(reviewer);

        let status = document.createElement("td");
        status.textContent = requests[i]["status"];
        row.appendChild(status);

        table.appendChild(row);
    }
    tableDiv.appendChild(table);
}

async function getRequests() {
    if(document.getElementById("user").value.length === 0) {

        const requests = await fetch("http://localhost:7000/allReimbursements/?" +
            "pending=" + document.getElementById("pending").checked +
            "&approved=" + document.getElementById("approved").checked +
            "&denied=" + document.getElementById("denied").checked)
            .then(response =>
                response.json());

        resetTable();

        if (requests.length > 0) {
            var table = document.createElement("table");
            table.id = "resultsTable";

            var headers = ["ID", "Requester", "Amount", "Business", "Location", "Reviewer"];
            var headerSection = document.createElement("tr");
            table.appendChild(headerSection);
            for (let i = 0; i < headers.length; i++) {
                var j = document.createElement("th");
                j.textContent = headers[i] + "  ";
                headerSection.appendChild(j);
            }
        }
        for (let i = 0; i < requests.length; i++) {
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

            let reviewer = document.createElement("td");
            reviewer.textContent = requests[i]["reviewer"]["firstname"] + " " + requests[i]["reviewer"]["lastname"];
            row.appendChild(reviewer);

            let status = document.createElement("td");
            status.textContent = requests[i]["status"];
            row.appendChild(status);

            table.appendChild(row);
        }
        tableDiv.appendChild(table);
    }
    else{
        getRequestsByUser();
    }
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

document.addEventListener("DOMContentLoaded", () => {
    const requestIdField = document.getElementById("requestID");

    const approveButton = document.getElementById("Approve");
    const denyButton = document.getElementById("Deny");

    const userField = document.getElementById("user");
    const searchButton = document.getElementById("searchUser");

    approveButton.addEventListener("click",(e) =>{
        e.preventDefault();
        let requestID = requestIdField.value;
        if(requestID.length < 1){
            alert(tableDiv.firstChild.firstChild.firstChild.nodeValue);
        }
        fetch("http://localhost:7000/adminResolveRequest/",{
            method:"POST",
            mode:"cors",
            body:JSON.stringify({
                id:requestIdField.value,
                status:"Approved",
                authority:loggedUser
            })
        }).then(getRequests);
    });

    denyButton.addEventListener("click",(e)=>{
        e.preventDefault();
        fetch("http://localhost:7000/adminResolveRequest/",{
            method:"POST",
            mode:"cors",
            body:JSON.stringify({
                id:requestIdField.value,
                status:"Denied",
                authority:loggedUser
            })
        }).then(getRequests);
    })

    userField.addEventListener("blur",()=>{
        const username = userField.value;
        fetch("http://localhost:7000/usernameAvailable/?username=" + username).then(response =>{
            response.text().then(function (text) {
                // do something with the text response
                if(!text.includes("unavailable")){
                    userField.classList.add("wrong");
                    userField.classList.remove("input-valid");
                }
                else{
                    userField.classList.remove("wrong");
                    userField.classList.add("input-valid");
                }
            }).then(getRequestsByUser);
        });
    })

    searchButton.addEventListener("click",(e) =>{
       e.preventDefault();
       getRequestsByUser();
    });

});