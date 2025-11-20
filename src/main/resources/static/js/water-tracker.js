document.addEventListener('DOMContentLoaded', function() {
    const glasses = document.querySelectorAll('.water-glass');
    const hiddenInput = document.getElementById('waterAmountInput');
    const displayAmount = document.getElementById('waterDisplay');
    let selectedAmount = 0;

    glasses.forEach((glass, index) => {
        const amount = (index + 1) * 250;

        glass.addEventListener('mouseenter', () => {
            highlightGlasses(index);
            displayAmount.textContent = `${amount} ml`;
        });

        glass.addEventListener('click', () => {
            selectedAmount = amount;
            hiddenInput.value = selectedAmount;
            updateGlassesVisuals(index);
        });
    });

    document.querySelector('.water-tracker').addEventListener('mouseleave', () => {
        const savedIndex = (selectedAmount / 250) - 1;
        updateGlassesVisuals(savedIndex);
        displayAmount.textContent = selectedAmount > 0 ? `${selectedAmount} ml` : '0 ml';
    });

    function highlightGlasses(endIndex) {
        glasses.forEach((glass, i) => {
            if (i <= endIndex) {
                glass.classList.add('filled');
            } else {
                glass.classList.remove('filled');
            }
        });
    }

    function updateGlassesVisuals(selectedIndex) {
        glasses.forEach((glass, i) => {
            if (i <= selectedIndex) {
                glass.classList.add('filled');
            } else {
                glass.classList.remove('filled');
            }
        });
    }
});