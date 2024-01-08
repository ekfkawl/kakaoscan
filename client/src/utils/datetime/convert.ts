function timestampToDate(timestamp: number): string {
    const date = new Date(timestamp * 1000);
    const formatter = new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        timeZone: 'Asia/Seoul',
    });

    return formatter.format(date);
}

export default timestampToDate;