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
	// Search Functionality --- Not yet working .. Need to work almost 99% done
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

// Payment Gateway Functionality
	// First request- to server to create order
	const paymentStart=()=>{
		console.log("Payment Start");
		let amount=$("#payment-input").val();
		console.log("Payment Start with: "+amount);
		if(amount==="" || amount==null){
			alert("Please enter amount");
			return;
		}
		$.ajax({

			url : '/user/createPaymentOrder',
			data: JSON.stringify({amount:amount,info:'order_request'}),
			contentType:"application/json",
			type:'POST',
			dataType:'json',
			success:function (response)
			{ //invoked when success
				console.log(response);
				if(response.status=="created"){
					// open payment form
					let option={
						key: '',
						amount:response.amount,
						currency:'INR',
						name: 'TRUST CARE PHARMACY',
						description:'Donation',
						image:'URL',
						order_id:response.id,
						handler: function (response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log("Payment Successful");
							alert("Congrats !! Payment Successful !!")
						},
						prefill: {
							name: "",
							email: "",
							contact: ""
						},
						notes: {
							"address": "Razorpay Corporate Office"
						},
						theme: {
							color: "#3399cc"
						}
					};
					let rzp= new Razorpay(options);
					rzp.on('payment.failed', function (response){
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						alert("Oops payment failed!! ")
					});
					rzp.open();
				}
			},
			error: function (error){
				// Invoked when we get error
				console.log(error);
				alert("Something went wrong !!")
			}
		})
	};


	// Step1- Razorpay Gateway ajax call with amount and order_request
	// Now to create order, we need to send request to server
	// We will use ajax to send request to server
	// The below URL will be created in the controller class which can accept amount and order_request both
	// Note: N- number of object we can pass here, and it will convert all the object into JSON
	// When success then calling a callback function and the result will be in response