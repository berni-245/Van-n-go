export function setParam(paramName, value) {
    const url = new URL(window.location.href);
    url.searchParams.set(paramName, value);
    return url.toString();
}

export function setParamAndNavigate(paramName, value) {
    window.location.href = setParam(paramName, value);
}

export function setParamDontNavigate(paramName, value) {
    const url = setParam(paramName, value)
    window.history.pushState({path: url}, null, url);
}