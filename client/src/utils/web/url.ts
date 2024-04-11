export const getAPIBaseURL = (): string => {
    const { protocol, hostname, port } = window.location;
    const updatedPort = port === '3000' ? '8080' : port;
    return `${protocol}//${hostname}${updatedPort ? `:${updatedPort}` : ''}`;
};
