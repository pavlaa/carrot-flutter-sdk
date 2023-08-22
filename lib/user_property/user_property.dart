class UserProperty {
  final String name;
  final String value;

  const UserProperty({required this.name, required this.value});
}

// extension UserPropertyExtensions on UserProperty {
//   Map<String, dynamic> toJson() {
//     Map<String, dynamic> json = {};
//     json[name] = value;
//     return json;
//   }
// }
