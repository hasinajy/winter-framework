#!/bin/bash

# Define the directory containing class files
bin_dir="../bin"
target_dir="../lib"

# Create the archive inside the target directory
jar -cvf $target_dir/winter.jar -C $bin_dir .

echo "Created winter.jar from $bin_dir"
read -p "Press any key to continue..."