

const toggle = () => {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");

    if (sidebar.style.display === "block" || sidebar.style.display === "") {
        sidebar.style.display = "none";
        content.style.marginLeft = "0";
    } else {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
};

const search=()=>{
    // console.log("called");
    let searchInput = document.getElementById("search-input");
    let searchResult = document.querySelector(".search-result");
    let query = searchInput.value;
    if(query==''){
        searchResult.style.display="none";
    }else{
        searchResult.style.display="block";
        let url = `http://localhost:8080/search/${query}`;
        fetch(url)
            .then(response => response.json())
            .then(data => {
                let text = `<div class='list-group'>`;
                
                data.forEach(element => {
                    text += `<a href='/user/contact/${element.cId}' class='list-group-item list-group-item-action'>${element.name}</a>`; 
                });
                
                text += `</div>`;
                searchResult.innerHTML = text;
            })
            .catch(error => {
                console.error("Error fetching data:", error);
                searchResult.innerHTML = `<p class='error-message'>Unable to fetch results</p>`;
            });
    }
}