#!/bin/bash

MACHINE_IP=$(hostname -I | cut -d ' ' -f 1)

sed -i "s/TEST_MACHINE_IP/$MACHINE_IP/g" orion_catalogue.json
sed -i "s/TEST_MACHINE_IP/$MACHINE_IP/g" test.js