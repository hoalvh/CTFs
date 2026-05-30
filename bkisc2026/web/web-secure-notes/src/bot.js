const puppeteer = require('puppeteer');
const CHALLENGE_URL = process.env.CHALLENGE_URL || 'http://localhost:3000';
const ADMIN_PASS = process.env.ADMIN_PASS || 'REDACTED';

async function visit(url) {
    console.log(`[bot] Launching...`);
    const browser = await puppeteer.launch({
        headless: 'new',
        args: ['--no-sandbox', '--disable-setuid-sandbox',
               '--disable-popup-blocking',
               '--disable-features=BlockInsecurePrivateNetworkRequests']
    });
    try {
        const page = await browser.newPage();
        // page.on('console', m => console.log(`[chrome] ${m.text()}`));

        await page.goto(`${CHALLENGE_URL}/login`, { waitUntil: 'networkidle0' });
        await page.type('input[name="username"]', 'admin');
        await page.type('input[name="password"]', ADMIN_PASS);
        await Promise.all([
            page.waitForNavigation({ waitUntil: 'networkidle0' }),
            page.click('button[type="submit"]')
        ]);
        console.log(`[bot] Logged in at ${page.url()}`);

        console.log(`[bot] Visiting: ${url}`);
        await page.goto(url, { waitUntil: 'domcontentloaded', timeout: 5000 })
            .catch(e => console.log(`[bot] goto: ${e.message}`));

        console.log(`[bot] Waiting 15s...`);
        await new Promise(r => setTimeout(r, 15000));
        console.log(`[bot] Done.`);
    } catch (e) {
        console.error(`[bot] ${e.message}`);
    } finally {
        await browser.close();
    }
}
module.exports = { visit };
