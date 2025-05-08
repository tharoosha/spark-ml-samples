document.addEventListener('DOMContentLoaded', function() {
    const lyricsForm = document.getElementById('lyrics-form');
    const resultsSection = document.getElementById('results-section');
    const loadingIndicator = document.getElementById('loading');
    const newAnalysisBtn = document.getElementById('new-analysis');
    const predictedGenreElement = document.getElementById('predicted-genre');
    const confidenceElement = document.getElementById('confidence');
    let genreChart = null;

    lyricsForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Show loading indicator
        loadingIndicator.style.display = 'block';
        
        // Prepare form data
        const formData = new FormData(lyricsForm);
        
        try {
            // Send POST request to backend
            const response = await fetch('/predict', {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) {
                throw new Error('Server error: ' + response.statusText);
            }
            
            // Parse JSON response
            const result = await response.json();
            
            // Update UI with results
            updateResults(result);
            
            // Hide loading indicator and form, show results
            loadingIndicator.style.display = 'none';
            lyricsForm.closest('.input-section').style.display = 'none';
            resultsSection.style.display = 'block';
            
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while analyzing the lyrics. Please try again.');
            loadingIndicator.style.display = 'none';
        }
    });

    newAnalysisBtn.addEventListener('click', function() {
        // Reset form
        lyricsForm.reset();
        
        // Hide results, show form
        resultsSection.style.display = 'none';
        lyricsForm.closest('.input-section').style.display = 'block';
        
        // Destroy existing chart to prevent memory leaks
        if (genreChart) {
            genreChart.destroy();
        }
    });

    // Function to update results UI
    function updateResults(data) {
        // Update predicted genre
        predictedGenreElement.textContent = data.genre;
        
        // Calculate and update confidence value
        const confidence = (data.probabilities[data.genre] * 100).toFixed(2);  // Confidence is a percentage
        confidenceElement.textContent = `${confidence}%`;  // Display confidence in percentage format
        
        // Create chart for visualization
        createChart(data.probabilities);
    }

    // Function to create pie chart visualization
    function createChart(probabilities) {
        const ctx = document.getElementById('genre-chart').getContext('2d');
        
        // Convert probabilities object to arrays for Chart.js
        const labels = Object.keys(probabilities);
        const data = Object.values(probabilities).map(val => (val * 100).toFixed(2)); // Convert to percentage
        
        // Filter out extremely small probabilities to keep the chart clean (optional)
        const validLabels = labels.filter((label, index) => data[index] > 0.01); // Only show probabilities > 0.01%
        const validData = data.filter(val => val > 0.01);

        // Generate red gradient for the pie chart
        const backgroundColors = validLabels.map((_, index) => {
            const opacity = 0.7 + (index / (validLabels.length * 2));
            return `rgba(255, 0, 0, ${opacity})`;  // Red colors
        });

        // Destroy previous chart instance if it exists
        if (genreChart) {
            genreChart.destroy();
        }
        
        // Create new pie chart
        genreChart = new Chart(ctx, {
            type: 'pie',  // Pie chart
            data: {
                labels: validLabels,
                datasets: [{
                    label: 'Genre Probability (%)',
                    data: validData,
                    backgroundColor: backgroundColors,
                    borderColor: 'rgba(255, 0, 0, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.raw}%`; // Format as percentage
                            }
                        }
                    },
                    legend: {
                        position: 'top',
                    }
                }
            }
        });
    }
});
