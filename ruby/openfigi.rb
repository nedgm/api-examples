# Copyright 2017 Bloomberg Finance L.P.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#     http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'net/https'
require 'json'

input = File.read('input.txt')
uri = URI.parse('https://api.openfigi.com/v3/mapping')
https = Net::HTTP.new(uri.host,uri.port)
https.use_ssl = true
request = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' => 'application/json'})
request.body = input
response = https.request(request)
puts JSON.pretty_generate(JSON.parse(response.body))
puts "\nPress Enter to close the window..."
gets