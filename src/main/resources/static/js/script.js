console.log("This is my SCM script file");
console.log("this is visual studio");


/*function toggleSidebar(){
 this is the old way of creating function but nowadays function is created using arrow==>
} */

const toggleSidebar = ()=> {
	if($(".sidebar").is(":visible")){
		// true 
		// hide the sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%")
	}else{
		// false 
		// show the sidebar
		$(".sidebar").css("display","block")
		$(".content").css("margin-left","20%")
	}
};
const search = () => {
	//console.log("Searching....")
	// Get value from the input text using id search-input and show and hide the result dive using its CSS class
	let query= $("#search-input").val();

	if(query === "".trim()){
		$(".search-result").hide();
	}else {
		//console.log(query);
		// Sending request to server , so get URL first
		let url= `http://localhost:8082/search/${query}`;
		// pass this url in the fetch(url) method, which hit the method and get the result in response
		// then convert into json and return to (data) and store into one data: data will contain the result in json
		fetch(url).then((response)=>{
			return response.json();
		}).then((data)=>{
			console.log(data);
			// Now let's send the result to html form back using below code
			//1- Create dive
			// 2- iterate the data with forEach and get the name
			// 3- inside forEach create anchor tag, so that on this click Contact detail page can be liked
			// 4- attach this result as html(text) using CSS class name as given below
			let text = `<div class= 'list-group'>`
				data.forEach((contact)=>{
					text+=`<a href='/user/${contact.cId}/contact' class="list-group-item list-group-item-action">${contact.name} </a>`;
				})
			text+=`</div>`
			$(".search-result").html(text)
		});
		$(".search-result").show();

	}
};
