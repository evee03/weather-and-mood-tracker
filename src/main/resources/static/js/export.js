const exportModal = document.getElementById('exportModal');

function openExportModal() {
    const today = new Date().toISOString().split('T')[0];
    const lastWeek = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

    document.getElementById('exportTo').value = today;
    document.getElementById('exportTo').max = today;
    document.getElementById('exportFrom').value = lastWeek;
    document.getElementById('exportFrom').max = today;

    exportModal.style.display = 'block';
}

function closeExportModal() {
    exportModal.style.display = 'none';
}

function triggerExport() {
    const modalElement = document.getElementById('exportModal');
    const msgRange = modalElement.getAttribute('data-msg-range');
    const msgOrder = modalElement.getAttribute('data-msg-order');
    const msgFuture = modalElement.getAttribute('data-msg-future');
    const msgStartFuture = modalElement.getAttribute('data-msg-start-future');
    const from = document.getElementById('exportFrom').value;
    const to = document.getElementById('exportTo').value;
    const format = document.querySelector('input[name="exportFormat"]:checked').value;
    const today = new Date().toISOString().split('T')[0];


    if (!from || !to) {
        alert(msgRange);
        return;
    }

    if (from > to) {
        alert(msgOrder);
        return;
    }

    if (to > today) {
        alert(msgFuture);
        return;
    }

    if (from > today) {
        alert(msgStartFuture);
        return;
    }

    const modalInstance = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
    modalInstance.hide();

    setTimeout(() => {
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());

        document.body.classList.remove('modal-open');
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';

        window.location.href = `/api/settings/export?from=${from}&to=${to}&format=${format}`;

    }, 300);
}

window.addEventListener('click', (e) => {
    if (e.target === exportModal) closeExportModal();
});