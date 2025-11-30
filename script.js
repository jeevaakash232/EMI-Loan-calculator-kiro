// Get DOM elements
const principalInput = document.getElementById('principal');
const rateInput = document.getElementById('rate');
const tenureInput = document.getElementById('tenure');

const principalRange = document.getElementById('principalRange');
const rateRange = document.getElementById('rateRange');
const tenureRange = document.getElementById('tenureRange');

const principalValue = document.getElementById('principalValue');
const rateValue = document.getElementById('rateValue');
const tenureValue = document.getElementById('tenureValue');

const form = document.getElementById('emiForm');
const result = document.getElementById('result');

const emiAmount = document.getElementById('emiAmount');
const totalAmount = document.getElementById('totalAmount');
const totalInterest = document.getElementById('totalInterest');
const principalAmount = document.getElementById('principalAmount');

let chart = null;

// Format number to Indian currency
function formatCurrency(amount) {
    return '₹' + amount.toLocaleString('en-IN', {
        maximumFractionDigits: 2,
        minimumFractionDigits: 2
    });
}

// Sync input and range values
function syncInputs() {
    // Principal
    principalRange.addEventListener('input', (e) => {
        principalInput.value = e.target.value;
        principalValue.textContent = formatCurrency(e.target.value);
    });
    
    principalInput.addEventListener('input', (e) => {
        const value = Math.min(Math.max(e.target.value, 10000), 10000000);
        principalRange.value = value;
        principalValue.textContent = formatCurrency(value);
    });

    // Rate
    rateRange.addEventListener('input', (e) => {
        rateInput.value = e.target.value;
        rateValue.textContent = e.target.value + '%';
    });
    
    rateInput.addEventListener('input', (e) => {
        const value = Math.min(Math.max(e.target.value, 1), 30);
        rateRange.value = value;
        rateValue.textContent = value + '%';
    });

    // Tenure
    tenureRange.addEventListener('input', (e) => {
        tenureInput.value = e.target.value;
        const years = Math.floor(e.target.value / 12);
        const months = e.target.value % 12;
        tenureValue.textContent = `${e.target.value} months (${years} years${months > 0 ? ' ' + months + ' months' : ''})`;
    });
    
    tenureInput.addEventListener('input', (e) => {
        const value = Math.min(Math.max(e.target.value, 12), 360);
        tenureRange.value = value;
        const years = Math.floor(value / 12);
        const months = value % 12;
        tenureValue.textContent = `${value} months (${years} years${months > 0 ? ' ' + months + ' months' : ''})`;
    });
}

// Calculate EMI
function calculateEMI(principal, annualRate, tenureMonths) {
    const monthlyRate = annualRate / (12 * 100);
    
    let emi;
    if (monthlyRate === 0) {
        emi = principal / tenureMonths;
    } else {
        const temp = Math.pow(1 + monthlyRate, tenureMonths);
        emi = (principal * monthlyRate * temp) / (temp - 1);
    }
    
    const total = emi * tenureMonths;
    const interest = total - principal;
    
    return {
        emi: Math.round(emi * 100) / 100,
        totalAmount: Math.round(total * 100) / 100,
        totalInterest: Math.round(interest * 100) / 100,
        principal: principal
    };
}

// Create pie chart
function createChart(principal, interest) {
    const canvas = document.getElementById('emiChart');
    const ctx = canvas.getContext('2d');
    
    // Clear previous chart
    if (chart) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
    
    // Set canvas size
    canvas.width = 300;
    canvas.height = 300;
    
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const radius = 100;
    
    const total = principal + interest;
    const principalAngle = (principal / total) * 2 * Math.PI;
    
    // Draw principal slice
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, radius, 0, principalAngle);
    ctx.closePath();
    ctx.fillStyle = '#667eea';
    ctx.fill();
    
    // Draw interest slice
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, radius, principalAngle, 2 * Math.PI);
    ctx.closePath();
    ctx.fillStyle = '#764ba2';
    ctx.fill();
    
    // Add labels
    ctx.font = 'bold 14px Arial';
    ctx.fillStyle = '#333';
    ctx.textAlign = 'center';
    
    // Principal label
    ctx.fillStyle = '#667eea';
    ctx.fillText('Principal', centerX, centerY + radius + 30);
    ctx.fillText(formatCurrency(principal), centerX, centerY + radius + 50);
    
    // Interest label
    ctx.fillStyle = '#764ba2';
    ctx.fillText('Interest', centerX, centerY + radius + 75);
    ctx.fillText(formatCurrency(interest), centerX, centerY + radius + 95);
    
    chart = true;
}

// Handle form submission
form.addEventListener('submit', (e) => {
    e.preventDefault();
    
    const principal = parseFloat(principalInput.value);
    const rate = parseFloat(rateInput.value);
    const tenure = parseInt(tenureInput.value);
    
    if (principal && rate && tenure) {
        const calculation = calculateEMI(principal, rate, tenure);
        
        // Display results
        emiAmount.textContent = formatCurrency(calculation.emi);
        totalAmount.textContent = formatCurrency(calculation.totalAmount);
        totalInterest.textContent = formatCurrency(calculation.totalInterest);
        principalAmount.textContent = formatCurrency(calculation.principal);
        
        // Show result section
        result.classList.remove('hidden');
        
        // Create chart
        createChart(calculation.principal, calculation.totalInterest);
        
        // Smooth scroll to result
        result.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
});

// Initialize
syncInputs();
