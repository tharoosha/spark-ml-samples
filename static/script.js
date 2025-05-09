document.getElementById("predictButton").addEventListener("click", function() {
    const lyrics = document.getElementById("lyrics").value;
    
    if (!lyrics.trim()) {
        alert("Please enter the lyrics.");
        return;
    }

    // Make the API request
    fetch("/music/predict", {  // Relative URL to the backend API
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ lyrics: lyrics })
    })
    .then(response => response.json())
    .then(data => {
        // Display the genre
        document.getElementById("genre").innerHTML = `<strong>Genre:</strong> ${data.genre}`;
        
        // Display probabilities
        let probabilitiesHtml = "<strong>Probabilities:</strong><br>";
        Object.keys(data).forEach(key => {
            if (key !== "genre") {
                probabilitiesHtml += `${key}: ${data[key]}<br>`;
            }
        });
        document.getElementById("probabilities").innerHTML = probabilitiesHtml;

        // Prepare data for the pie chart
        const genres = ["pop", "country", "blues", "rock", "jazz", "reggae", "hipHop", "kPop"];
        const probabilities = genres.map(genre => data[`${genre}Probability`]);

        // Render the pie chart using Chart.js
        const ctx = document.getElementById("pieChart").getContext("2d");
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: genres,
                datasets: [{
                    label: 'Genre Probabilities',
                    data: probabilities,
                    backgroundColor: [
                        "#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#FF8333", "#33FFF0", "#FFEB33", "#6A33FF"
                    ],
                    borderColor: '#fff',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(tooltipItem) {
                                return tooltipItem.label + ": " + tooltipItem.raw.toExponential(2);
                            }
                        }
                    }
                }
            }
        });
    })
    .catch(error => {
        console.error("Error:", error);
    });
});
