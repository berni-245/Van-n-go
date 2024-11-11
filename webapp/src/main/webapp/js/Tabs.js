import {setParamDontNavigate} from './setParams.js'

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[role="tab"]').forEach(tab => {
        const tabName = tab.getAttribute('aria-controls');
        tab.addEventListener('click', () => setParamDontNavigate('activeTab', tabName));
    });
})
