import {setParamAndNavigate} from './setParams.js'

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.pagination').forEach(pagination => {
        const paramName = pagination.getAttribute('param-name');
        const currentPage = parseInt(pagination.getAttribute('current-page'));
        pagination.querySelector('.pagination-prev').addEventListener(
            'click',
            () => setParamAndNavigate(paramName, currentPage - 1)
        );
        pagination.querySelector('.pagination-next').addEventListener(
            'click',
            () => setParamAndNavigate(paramName, currentPage + 1)
        );
        pagination.querySelectorAll('.pagination-page').forEach(p => {
            p.addEventListener('click', () => setParamAndNavigate(paramName, p.textContent));
        })
    });
})
