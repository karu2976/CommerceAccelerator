#!/bin/sh
echo "Enter site map generator Location:"

read sitemap_generator_location

echo "Enter site map generator config file Location:"

read config_file_location

${sitemap_generator_location}/RunSitemapGen.sh ${config_file_location}
