import pandas as pd

mendeley_df = pd.read_csv("./Mendeley_dataset.csv")
student_df = pd.read_csv("./Student_dataset.csv")

# Change the values in the 'genre' column to 'k-pop'
student_df['genre'] = 'k-pop'

mendeley_df = mendeley_df[
    ["artist_name", "track_name", "release_date", "genre", "lyrics"]
]
merged_dataset = pd.concat([mendeley_df, student_df], ignore_index=True)

merged_dataset.to_csv("Merged_dataset.csv", index=False)