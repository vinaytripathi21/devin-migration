/*
 *
 *    Copyright IBM Corp. 2023
 *
 */

/**
 * Application Configuration
 */
function springBackend() {
    const choice = new URLSearchParams(window.location.search).get('backend');
    if (choice) {
        localStorage.setItem('bankz.backend', choice);
    }
    return localStorage.getItem('bankz.backend') === 'spring';
}

export const config = {
    api: {
        // Base URL for API endpoints.
        // - Docker dev (port 3001): use relative '/api' so requests are proxied
        //   by nginx to the zosConnect container at zosConnect:9080/api/*.
        // - z/OS Liberty: frontend (FEBOZ) and API (BAQBOZ) are on separate
        //   Liberty instances. Match the protocol used to load the frontend page:
        //     https://host:9445 → https://host:9444/api
        //     http://host:9081  → http://host:9080/api
        // - Spring Boot (src/modern/bankz-app): open the page with ?backend=spring
        //   (remembered in localStorage; ?backend=zos switches back).
        baseUrl: springBackend()
            ? 'http://localhost:8085/api'
            : window.location.port === '3001'
            ? '/api'
            : window.location.protocol + '//' + window.location.hostname + ':' +
              (window.location.protocol === 'https:' ? '9444' : '9080') + '/api'
    },
    defaults: {
        sortCode: '987654'
    }
};

// Made with Bob
