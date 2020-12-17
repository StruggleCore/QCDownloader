# QCDownloader
API consumer and file downloader for qualitycontrol.cc

## About
This program allows a user to browse and download scenes from qualitycontrol.cc more effectively than their website with automatic quota hit stopping.
It's written in Java and is currently command line only. I'm a bit too lazy to offer compiled versions so if you're smart enough to run it (and thus smart enough to automate this yourself) but too lazy to actually do it, this is for you.

## Features
* Pulls all scenes into a cached json file for bulk searching of text.
* Automatic downloading of all scenes into a folder structure.
   - Automatically stops when you've hit your daily quota and gives an estimated time 
* Importing of previously downloaded content from several years ago when the site was flatfile.
* [TODO] Downloading of scenes based on model and other attributes.
* [TODO] GUI

## Objections
This is not meant for piracy. You should not be reuploading anything you download with this tool to anywhere, as that's illegal. This is solely for data preservation.  
The whole QC archive (videos and images) as of mid-2020 takes up about 300GB and takes about 1.32 months to download with the current 5GB/12hrs quota if you are relatively consistent with starting downloads on the quota reset mark.  
This program may also work on QC's sister sites assuming they use the same underlying API, which they might as the same webmaster is listed and the preview pages look the same. Your mileage may vary.

## Usage
1. Put it in an IDE or compile it.
2. Copy the `config.json.example` to `config.json` in the base folder. Fill out the `USERNAME`, `PASSWORD`, `USER_AGENT`, and `OUTPUT_DIR` config fields with your site username, password, browser user agent, and path to a folder respectively.
3. Run the program.
4. Choose an option from the menu.

## Contributing & Issues
Contributions are welcome, in the form of a pull request.
If the program stops working due to an API change, please do let me know. I may not be able to immediately fix it myself but if you're willing to bankroll a month of subscription I'll fix it posthaste.