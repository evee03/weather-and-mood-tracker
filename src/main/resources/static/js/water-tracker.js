document.addEventListener('DOMContentLoaded', function() {
    const container = document.querySelector('.water-container');
    const glasses = document.querySelectorAll('.water-glass');
    const hiddenInput = document.getElementById('hydrationInput');
    const displayAmount = document.getElementById('waterDisplay');

    let savedAmount = 0;

    if (!container) return;

    glasses.forEach((glass, index) => {
        const amount = (index + 1) * 250;

        glass.addEventListener('mouseenter', () => {
            updateVisuals(amount);
            displayAmount.textContent = `${amount} ml`;
            displayAmount.classList.add('text-muted');
        });

        glass.addEventListener('click', () => {
            savedAmount = amount;
            hiddenInput.value = savedAmount;

            glass.style.transform = "scale(1.3)";
            setTimeout(() => glass.style.transform = "scale(1.1)", 100);

            updateVisuals(savedAmount);
            displayAmount.textContent = `${savedAmount} ml`;
            displayAmount.classList.remove('text-muted');
        });
    });

    container.addEventListener('mouseleave', () => {
        updateVisuals(savedAmount);
        displayAmount.textContent = savedAmount > 0 ? `${savedAmount} ml` : '0 ml';
        displayAmount.classList.remove('text-muted');
    });

    function updateVisuals(targetAmount) {
        glasses.forEach(glass => {
            const glassAmount = parseInt(glass.getAttribute('data-amount'));
            if (glassAmount <= targetAmount) {
                glass.classList.add('filled');
                glass.style.opacity = "1";
                glass.style.filter = "grayscale(0%)";
            } else {
                glass.classList.remove('filled');
                glass.style.opacity = "0.3";
                glass.style.filter = "grayscale(100%)";
            }
        });
    }

    window.setHydration = function(amount) {
        savedAmount = amount || 0;
        hiddenInput.value = savedAmount;
        updateVisuals(savedAmount);
        displayAmount.textContent = `${savedAmount} ml`;
    };
});